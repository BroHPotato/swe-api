package com.redroundrobin.thirema.apirest.controller;

import com.redroundrobin.thirema.apirest.models.postgres.User;
import com.redroundrobin.thirema.apirest.models.postgres.ViewGraph;
import com.redroundrobin.thirema.apirest.service.postgres.ViewGraphService;
import com.redroundrobin.thirema.apirest.service.postgres.UserService;
import com.redroundrobin.thirema.apirest.utils.JwtUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.redroundrobin.thirema.apirest.utils.exception.ElementNotFoundException;
import com.redroundrobin.thirema.apirest.utils.exception.InvalidFieldsException;
import com.redroundrobin.thirema.apirest.utils.exception.MissingFieldsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {"/viewGraphs"})
public class ViewGraphController extends CoreController {

  private ViewGraphService viewGraphService;

  @Autowired
  public ViewGraphController(ViewGraphService viewGraphService) {
    this.viewGraphService = viewGraphService;
  }

  //tutti i viewGraphs
  @GetMapping(value = {""})
  public ResponseEntity<List<ViewGraph>> getViewGraphs(
      @RequestHeader("authorization") String authorization,
      @RequestParam(value = "user", required = false) Integer userId,
      @RequestParam(value = "view", required = false) Integer viewId) {
    User user = this.getUserFromAuthorization(authorization);
    if (user.getType() == User.Role.ADMIN) {
      if (userId != null && viewId != null) {
        return ResponseEntity.ok(viewGraphService.findAllByUserIdAndViewId(userId,viewId));
      } else if (userId != null) {
        return ResponseEntity.ok(viewGraphService.findAllByUserId(userId));
      } else if (viewId != null) {
        return ResponseEntity.ok(viewGraphService.findAllByViewId(viewId));
      } else {
        return ResponseEntity.ok(viewGraphService.findAll());
      }
    } else if (viewId == null && (userId == null || user.getId() == userId)) {
      return ResponseEntity.ok(viewGraphService.findAllByUserId(user.getId()));
    } else if (viewId != null && (userId == null || user.getId() == userId)) {
      return ResponseEntity.ok(viewGraphService.findAllByUserIdAndViewId(user.getId(),viewId));
    } else {
      return ResponseEntity.ok(Collections.emptyList());
    }
  }

  @GetMapping(value = {"/{viewGraphId:.+}"})
  public ResponseEntity<ViewGraph> getViewGraph(
      @RequestHeader("authorization") String authorization,
      @PathVariable("viewGraphId") int viewGraphId) {
    User user = this.getUserFromAuthorization(authorization);
    try {
      if (user.getType() == User.Role.ADMIN
          || viewGraphService.getPermissionByIdAndUserId(viewGraphId, user.getId())) {
        return ResponseEntity.ok(viewGraphService.findById(viewGraphId));
      } else {
        return new ResponseEntity(HttpStatus.FORBIDDEN);
      }
    } catch (ElementNotFoundException nfe) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping(value = {""})
  public ResponseEntity<ViewGraph> createUserViewGraphs(
      @RequestHeader("authorization") String authorization,
      @RequestBody Map<String, Integer> newViewGraphFields) {
    User user = this.getUserFromAuthorization(authorization);
    try {
      return ResponseEntity.ok(viewGraphService.createViewGraph(user, newViewGraphFields));
    } catch (MissingFieldsException | InvalidFieldsException fe) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping(value = {"/{viewGraphId:.+}"})
  public ResponseEntity<ViewGraph> editViewGraph(
      @RequestHeader("authorization") String authorization,
      @PathVariable("viewGraphId") int viewGraphId,
      @RequestBody Map<String, Integer> newViewGraphFields) {
    User user = this.getUserFromAuthorization(authorization);
    try {
      if (user.getType() == User.Role.ADMIN
        || viewGraphService.getPermissionByIdAndUserId(viewGraphId, user.getId())) {
        return ResponseEntity.ok(viewGraphService.editViewGraph(user, viewGraphId, newViewGraphFields));
      } else {
        return new ResponseEntity(HttpStatus.FORBIDDEN);
      }
    } catch (ElementNotFoundException | MissingFieldsException | InvalidFieldsException fe) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping(value = {"/{viewGraphId:.+}"})
  public ResponseEntity deleteUserViewGraph(
      @RequestHeader("authorization") String authorization,
      @PathVariable("viewGraphId") int viewGraphId) {
    User user = this.getUserFromAuthorization(authorization);
    try {
      if (user.getType() == User.Role.ADMIN
          || viewGraphService.getPermissionByIdAndUserId(viewGraphId, user.getId())) {
        if (viewGraphService.deleteViewGraph(viewGraphId)) {
          return new ResponseEntity(HttpStatus.OK);
        } else {
          return new ResponseEntity(HttpStatus.CONFLICT);
        }
      } else {
        return new ResponseEntity(HttpStatus.FORBIDDEN);
      }
    } catch (ElementNotFoundException nfe) {
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  }
}