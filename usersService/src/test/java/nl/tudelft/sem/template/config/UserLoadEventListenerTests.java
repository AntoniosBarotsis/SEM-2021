package nl.tudelft.sem.template.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.tudelft.sem.template.entities.Admin;
import nl.tudelft.sem.template.entities.Company;
import nl.tudelft.sem.template.entities.Student;
import nl.tudelft.sem.template.enums.Role;
import org.hibernate.event.spi.PostLoadEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class UserLoadEventListenerTests {

    private final transient Student student = new Student();
    private final transient Company company = new Company();
    private final transient Admin admin = new Admin();

    private final transient UserLoadEventListener inst = UserLoadEventListener.INSTANCE;
    private final transient PostLoadEvent event = Mockito.mock(PostLoadEvent.class);

    @Test
    void testSetRoleStudent() {
        Mockito.when(event.getEntity()).thenReturn(student);
        inst.onPostLoad(event);
        assertEquals(Role.STUDENT, student.getRole());
    }

    @Test
    void testSetRoleAdmin() {
        Mockito.when(event.getEntity()).thenReturn(admin);
        inst.onPostLoad(event);
        assertEquals(Role.ADMIN, admin.getRole());
    }

    @Test
    void testSetRoleCompany() {
        Mockito.when(event.getEntity()).thenReturn(company);
        inst.onPostLoad(event);
        assertEquals(Role.COMPANY, company.getRole());
    }
}
