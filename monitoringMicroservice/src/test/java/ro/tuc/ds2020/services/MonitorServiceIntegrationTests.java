//package ro.tuc.ds2020.services;
//
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.jdbc.Sql;
//import ro.tuc.ds2020.Ds2020TestConfig;
//import ro.tuc.ds2020.dtos.MonitorDTO;
//import ro.tuc.ds2020.dtos.MonitorDetailsDTO;
//
//import static org.springframework.test.util.AssertionErrors.assertEquals;
//
//import java.util.List;
//import java.util.UUID;
//
//@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/test-sql/create.sql")
//@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:/test-sql/delete.sql")
//public class MonitorServiceIntegrationTests extends Ds2020TestConfig {
//
//    @Autowired
//    MonitorService monitorService;
//
//    @Test
//    public void testGetCorrect() {
//        List<MonitorDTO> monitorDTOList = monitorService.findPersons();
//        assertEquals("Test Insert Monitor", 1, monitorDTOList.size());
//    }
//
//    @Test
//    public void testInsertCorrectWithGetById() {
//        MonitorDetailsDTO p = new MonitorDetailsDTO("John", "Somewhere Else street", 22);
//        UUID insertedID = monitorService.insert(p);
//
//        MonitorDetailsDTO insertedPerson = new MonitorDetailsDTO(insertedID, p.getName(),p.getAddress(), p.getAge());
//        MonitorDetailsDTO fetchedPerson = monitorService.findPersonById(insertedID);
//
//        assertEquals("Test Inserted Monitor", insertedPerson, fetchedPerson);
//    }
//
//    @Test
//    public void testInsertCorrectWithGetAll() {
//        MonitorDetailsDTO p = new MonitorDetailsDTO("John", "Somewhere Else street", 22);
//        monitorService.insert(p);
//
//        List<MonitorDTO> monitorDTOList = monitorService.findPersons();
//        assertEquals("Test Inserted Persons", 2, monitorDTOList.size());
//    }
//}
