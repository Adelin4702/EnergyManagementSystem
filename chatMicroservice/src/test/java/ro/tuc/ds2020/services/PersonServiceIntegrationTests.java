//package ro.tuc.ds2020.services;
//
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.jdbc.Sql;
//import ro.tuc.ds2020.Ds2020TestConfig;
//import ro.tuc.ds2020.dtos.ChatDTO;
//import ro.tuc.ds2020.dtos.ChatDetailsDTO;
//
//import static org.springframework.test.util.AssertionErrors.assertEquals;
//
//import java.util.List;
//import java.util.UUID;
//
//@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/test-sql/create.sql")
//@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:/test-sql/delete.sql")
//public class ChatServiceIntegrationTests extends Ds2020TestConfig {
//
//    @Autowired
//    ChatService chatService;
//
//    @Test
//    public void testGetCorrect() {
//        List<ChatDTO> chatDTOList = chatService.findChats();
//        assertEquals("Test Insert Chat", 1, chatDTOList.size());
//    }
//
//    @Test
//    public void testInsertCorrectWithGetById() {
//        ChatDetailsDTO p = new ChatDetailsDTO("1st user", true);
//        UUID insertedID = chatService.insert(p);
//
//        ChatDetailsDTO insertedChat = new ChatDetailsDTO(insertedID, p.getName(), p.isAdmin());
//        ChatDetailsDTO fetchedChat = chatService.findChatById(insertedID);
//
//        assertEquals("Test Inserted Chat", insertedChat, fetchedChat);
//    }
//
//    @Test
//    public void testInsertCorrectWithGetAll() {
//        ChatDetailsDTO p = new ChatDetailsDTO("2nd user", true);
//        chatService.insert(p);
//
//        List<ChatDTO> chatDTOList = chatService.findChats();
//        assertEquals("Test Inserted Chats", 2, chatDTOList.size());
//    }
//}
