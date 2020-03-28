package com.redroundrobin.thirema.apirest.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.redroundrobin.thirema.apirest.utils.exception.*;
import com.redroundrobin.thirema.apirest.models.postgres.User;
import com.redroundrobin.thirema.apirest.models.postgres.View;
import com.redroundrobin.thirema.apirest.service.postgres.EntityService;
import com.redroundrobin.thirema.apirest.service.postgres.UserService;
import com.redroundrobin.thirema.apirest.service.postgres.ViewService;
import com.redroundrobin.thirema.apirest.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ViewController {

  private JwtUtil jwtTokenUtil;

  private UserService userService;

  private ViewService viewService;

  @Autowired
  public ViewController(JwtUtil jwtTokenUtil, UserService userService,
                        ViewService viewService) {
    this.jwtTokenUtil = jwtTokenUtil;
    this.userService = userService;
    this.viewService = viewService;
  }

  //qualsiasi utente puo avere views da adr, anche admin
  @GetMapping(value = {"/views"})
  public ResponseEntity<?> views(@RequestHeader("Authorization") String authorization) {
    String token = authorization.substring(7);
    User user = userService.findByEmail(jwtTokenUtil.extractUsername(token));
    return ResponseEntity.ok(viewService.findByUserId(user.getUserId()));
  }


  @GetMapping(value = {"/view/{viewId:.+}"})
  public ResponseEntity<?> selectOneView(
      @RequestHeader("Authorization") String authorization,  @PathVariable("viewId") int viewId) {
    String token = authorization.substring(7);
    User user = userService.findByEmail(jwtTokenUtil.extractUsername(token));
    try {
      return ResponseEntity.ok(viewService.getViewByUserId(user.getUserId(), viewId));
    }
    catch(ViewNotFoundException e) {
      return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    catch(ValuesNotAllowedException e) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }


  @PostMapping(name = "/views/create")
  public ResponseEntity<?> views(
      @RequestHeader("Authorization") String authorization,  @RequestBody String rawNewView) {
    String token = authorization.substring(7);
    User user = userService.findByEmail(jwtTokenUtil.extractUsername(token));
    JsonObject jsonNewView = JsonParser.parseString(rawNewView).getAsJsonObject();
    try {
      return ResponseEntity.ok(viewService.serializeView(jsonNewView, user));
    }
    catch(KeysNotFoundException | MissingFieldsException e) {
      return new ResponseEntity(e, HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping(name = "/views/delete/{viewId:.+}")
  public ResponseEntity<?> deleteView(
      @RequestHeader("Authorization") String authorization,  @PathVariable("viewId") int viewToDeleteId) {
    String token = authorization.substring(7);
    User user = userService.findByEmail(jwtTokenUtil.extractUsername(token));
    try {
      viewService.deleteView(user, viewToDeleteId);
      return ResponseEntity.ok("deleted view succesfully");
    }
    catch(NotAuthorizedToDeleteUserException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    catch(ValuesNotAllowedException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }
}
