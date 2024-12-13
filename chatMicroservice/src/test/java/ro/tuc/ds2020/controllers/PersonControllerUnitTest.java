//package ro.tuc.ds2020.controllers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import ro.tuc.ds2020.Ds2020TestConfig;
//import ro.tuc.ds2020.dtos.ChatDetailsDTO;
//import ro.tuc.ds2020.services.ChatService;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//public class ChatControllerUnitTest extends Ds2020TestConfig {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ChatService service;
//
//    @Test
//    public void insertChatTest() throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        ChatDetailsDTO chatDTO = new ChatDetailsDTO( "John", true);
//
//        mockMvc.perform(post("/chat")
//                .content(objectMapper.writeValueAsString(chatDTO))
//                .contentType("application/json"))
//                .andExpect(status().isCreated());
//    }
//
//    @Test
//    public void insertChatTestFailsDueToAge() throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        ChatDetailsDTO chatDTO = new ChatDetailsDTO("john", false);
//
//        mockMvc.perform(post("/chat")
//                .content(objectMapper.writeValueAsString(chatDTO))
//                .contentType("application/json"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    public void insertChatTestFailsDueToNull() throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        ChatDetailsDTO chatDTO = new ChatDetailsDTO( "avvd", false);
//
//        mockMvc.perform(post("/chat")
//                .content(objectMapper.writeValueAsString(chatDTO))
//                .contentType("application/json"))
//                .andExpect(status().isBadRequest());
//    }
//}
