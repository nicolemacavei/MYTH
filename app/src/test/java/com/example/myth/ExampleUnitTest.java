package com.example.myth;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

//    In the first test case, we create a new User object and verify that the getter methods
//    return the expected values.
    //@Test
//    public void testUserCreation() {
//        User user = new User("johndoe@example.com", "password1", "Doe", "John");
//        assertEquals("password1", user.getPassword());
//        assertEquals("John", user.getFirstName());
//        assertEquals("Doe", user.getLastName());
//        assertEquals("johndoe@example.com", user.getEmail());
//    }
//
//// we attempt to create a User with an invalid email address and verify that
//// an IllegalArgumentException is thrown with the expected error message.
//    @Test
//    public void testUserInvalidEmail() {
//        try {
//            User user = new User("invalidemail", "password1", "Doe", "Joe");
//            fail("Expected IllegalArgumentException to be thrown");
//        } catch (IllegalArgumentException e) {
//            assertEquals("Invalid email address", e.getMessage());
//        }
//    }
//
//    //In this case we test the password function to send the message.
//    @Test
//    public void testUserInvalidPassword(){
//        try {
//            User user = new User("johndoe@example.com", "password", "Doe", "Joe");
//            fail("Expected IllegalArgumentException to be thrown");
//        } catch (IllegalArgumentException e) {
//            assertEquals("Invalid password", e.getMessage());
//        }
//    }
//
//    //In this case we test the password function to send the invalid message.
//    @Test
//    public void testUserInvalidPassword2(){
//        try {
//            User user = new User("johndoe@example.com", "1111", "Doe", "Joe");
//            fail("Expected IllegalArgumentException to be thrown");
//        } catch (IllegalArgumentException e) {
//            assertEquals("Invalid password", e.getMessage());
//        }
//    }
}