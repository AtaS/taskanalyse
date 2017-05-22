package com.ptc.taskanalyse.controllers;

import com.ptc.taskanalyse.models.BooleanResponse;
import com.ptc.taskanalyse.models.SimpleResponse;
import com.ptc.taskanalyse.models.Task;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


/**
 * Created by asasmaz on 22/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class TasksControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetTask() throws Exception {
        ResponseEntity<Task> entity = this.restTemplate.getForEntity("/tasks/1", Task.class);
        Assert.assertEquals(1, entity.getBody().getId());

        ResponseEntity<Task> entity2 = this.restTemplate.getForEntity("/tasks/2", Task.class);
        Assert.assertEquals(2, entity2.getBody().getId());
    }

    @Test
    public void testPerformTaskAndAverage() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.set("duration", "5");

        ResponseEntity<BooleanResponse> entity = this.restTemplate.postForEntity("/tasks/1/perform", params, BooleanResponse.class);
        Assert.assertEquals(true, entity.getBody().getResult());

        ResponseEntity<SimpleResponse> taskEntity = this.restTemplate.getForEntity("/tasks/1/average", SimpleResponse.class);
        Assert.assertEquals(5.0, taskEntity.getBody().getData());


        params.clear();
        params.set("duration", "9");
        entity = this.restTemplate.postForEntity("/tasks/1/perform", params, BooleanResponse.class);
        Assert.assertEquals(true, entity.getBody().getResult());

        taskEntity = this.restTemplate.getForEntity("/tasks/1/average", SimpleResponse.class);
        Assert.assertEquals(7.0, taskEntity.getBody().getData());
    }

}