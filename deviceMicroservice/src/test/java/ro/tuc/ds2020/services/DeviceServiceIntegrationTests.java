//package ro.tuc.ds2020.services;
//
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.jdbc.Sql;
//import ro.tuc.ds2020.Ds2020TestConfig;
//import ro.tuc.ds2020.dtos.DeviceDTO;
//import ro.tuc.ds2020.dtos.DeviceDetailsDTO;
//
//import static org.springframework.test.util.AssertionErrors.assertEquals;
//
//import java.util.List;
//import java.util.UUID;
//
//@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/test-sql/create.sql")
//@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:/test-sql/delete.sql")
//public class DeviceServiceIntegrationTests extends Ds2020TestConfig {
//
//    @Autowired
//    DeviceService deviceService;
//
//    @Test
//    public void testGetCorrect() {
//        List<DeviceDTO> deviceDTOList = deviceService.findDevices();
//        assertEquals("Test Insert Device", 0, deviceDTOList.size());
//    }
//
//    @Test
//    public void testInsertCorrectWithGetById() {
//        DeviceDetailsDTO p = new DeviceDetailsDTO("device", "Somewhere Else street", 22, null);
//        UUID insertedID = deviceService.insert(p);
//
//        DeviceDetailsDTO insertedDevice = new DeviceDetailsDTO(insertedID, p.getDescription(),p.getAddress(), p.getMaxHourlyEnergyConsumption(), null);
//        DeviceDetailsDTO fetchedDevice = deviceService.findDeviceById(insertedID);
//
//        assertEquals("Test Inserted Device", insertedDevice, fetchedDevice);
//    }
//
//    @Test
//    public void testInsertCorrectWithGetAll() {
//        DeviceDetailsDTO p = new DeviceDetailsDTO("device 2", "Somewhere Else street", 25, null);
//        deviceService.insert(p);
//
//        List<DeviceDTO> deviceDTOList = deviceService.findDevices();
//        assertEquals("Test Inserted Devices", 1, deviceDTOList.size());
//    }
//}
