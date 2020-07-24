package io.floodplain.miroassignment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.floodplain.miroassignment.impl.RateLimitImplementation;
import io.floodplain.miroassignment.impl.WidgetServiceImpl;
import io.floodplain.miroassignment.model.RateLimitResponse;
import io.floodplain.miroassignment.model.RateLimiter;
import io.floodplain.miroassignment.model.Widget;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {WidgetController.class, WidgetServiceImpl.class, RateLimitImplementation.class})
public class WidgetIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(WidgetIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RateLimiter rateLimiter;

    @Test
    public void testWidgetCRUD() throws Exception {

        // List widgets, should be zero
        assertEquals (0, listWidgets().size());



        // Add a widget
        Widget w = new Widget(1,2,3,4, Instant.ofEpochSecond(1000000));
        MvcResult insertResult = mockMvc.perform(post("/widget")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(w)))
                .andExpect(status().isOk())
                .andReturn();
        String id = insertResult.getResponse().getContentAsString();
        logger.info("Detected id: {}",id);

        // Query widget again, should be identical to original
        MvcResult queryResult = mockMvc.perform(get("/widget/{id}", id)
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        Widget reQueried = objectMapper.readValue(queryResult.getResponse().getContentAsByteArray(),Widget.class);
        assertEquals(w,reQueried);

        // List widgets, should contain one widget now
        assertEquals (1, listWidgets().size());

        mockMvc.perform(delete("/widget/{id}",id))
                .andExpect(status().isOk());

        // deleted, should be empty again
        assertEquals (0, listWidgets().size());

//        mockMvc.perform(post("/widget/{id}", 42L)
//                .contentType("application/json")
////                .param("sendWelcomeMail", "true")
//                .content(objectMapper.writeValueAsString(w)))
//                .andExpect(status().isOk());

    }

    @Test
    public void testActualRateLimiting() throws Exception {
        // start with 10 tokens, good for two lists.
        rateLimiter.setMaxRequestsPerMinute(10);
        for (int i=0; i < 2; i++) {
            mockMvc.perform(get("/widget"))
                    .andExpect(status().isOk());
        }
        // next one should fail
        mockMvc.perform(get("/widget"))
                .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()));

    }

    private List<Widget> listWidgets() throws Exception {
        byte[] listResponse = mockMvc.perform(get("/widget"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();
        return objectMapper.readValue(listResponse, new TypeReference<List<Widget>>(){});
    }
}
