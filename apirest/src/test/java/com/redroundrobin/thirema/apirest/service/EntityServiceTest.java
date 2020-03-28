package com.redroundrobin.thirema.apirest.service;

import com.redroundrobin.thirema.apirest.models.postgres.Entity;
import com.redroundrobin.thirema.apirest.models.postgres.Sensor;
import com.redroundrobin.thirema.apirest.repository.postgres.EntityRepository;
import com.redroundrobin.thirema.apirest.service.postgres.EntityService;
import com.redroundrobin.thirema.apirest.service.postgres.SensorService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class EntityServiceTest {

  @MockBean
  private EntityRepository repo;

  @MockBean
  private SensorService sensorService;

  private EntityService entityService;


  private Entity entity1;
  private Entity entity2;
  private Entity entity3;

  private Sensor sensor1;
  private Sensor sensor2;
  private Sensor sensor3;


  @Before
  public void setUp() {
    entityService = new EntityService(repo);
    entityService.setSensorService(sensorService);

    sensor1 = new Sensor();
    sensor1.setId(1);

    sensor2 = new Sensor();
    sensor2.setId(2);

    sensor3 = new Sensor();
    sensor3.setId(3);

    List<Sensor> allSensors = new ArrayList<>();
    allSensors.add(sensor1);
    allSensors.add(sensor2);
    allSensors.add(sensor3);
    List<Sensor> sensors1 = new ArrayList<>();
    sensors1.add(sensor1);
    sensors1.add(sensor2);
    List<Sensor> sensors2 = new ArrayList<>();
    sensors2.add(sensor3);

    entity1 = new Entity();
    entity1.setId(1);
    entity1.setName("entity1");
    entity1.setSensors(sensors1);

    entity2 = new Entity();
    entity2.setId(2);
    entity2.setName("entity2");
    entity2.setSensors(sensors2);

    entity3 = new Entity();
    entity3.setId(3);
    entity3.setName("entity3");
    entity3.setSensors(sensors1);

    List<Entity> entities = new ArrayList<>();
    entities.add(entity1);
    entities.add(entity2);
    entities.add(entity3);

    when(repo.findAll()).thenReturn(entities);
    when(repo.findById(anyInt())).thenAnswer(i -> {
      return entities.stream().filter(e -> i.getArgument(0).equals(e.getId()))
          .findFirst();
    });
    when(sensorService.findById(anyInt())).thenAnswer(i -> {
      return allSensors.stream().filter(s -> i.getArgument(0).equals(s.getId()))
          .findFirst().orElse(null);
    });
    when(repo.findAllBySensors(any(Sensor.class))).thenAnswer(i -> {
      return entities.stream().filter(e -> e.getSensors().contains(i.getArgument(0)))
          .collect(Collectors.toList());
    });
  }

  @Test
  public void findAllEntities() {
    List<Entity> entities = entityService.findAll();

    assertTrue(!entities.isEmpty());
  }

  @Test
  public void findAllEntitiesBySensorId() {
    List<Entity> entities = entityService.findAllBySensorId(sensor1.getId());

    assertTrue(entities.stream().count() == 2);
  }

  @Test
  public void findAllEntitiesBySensorIdEmptyResult() {
    List<Entity> entities = entityService.findAllBySensorId(4);

    assertTrue(entities.stream().count() == 0);
  }

  @Test
  public void findEntityById() {
    Entity entity = entityService.findById(entity1.getId());

    assertNotNull(entity);
  }
}
