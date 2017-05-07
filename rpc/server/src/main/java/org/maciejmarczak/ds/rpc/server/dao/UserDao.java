package org.maciejmarczak.ds.rpc.server.dao;

import org.maciejmarczak.ds.rpc.server.protos.Contact;
import org.maciejmarczak.ds.rpc.server.protos.User;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UserDao {
    public User getById(String id) {
        return UserMockData.USERS.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<User> getAllPatients() {
        return getAllByRole(User.Role.PATIENT);
    }

    private List<User> getAllByRole(User.Role role) {
        return UserMockData.USERS.stream()
                .filter(u -> u.getRole() == role)
                .collect(Collectors.toList());
    }

    static class UserMockData {
        static final List<User> USERS = Arrays.asList(
                createUser("100", "Adam Nowak", createContact(), User.Role.DOCTOR),
                createUser("101", "Jan Kowalski", createContact(), User.Role.PATIENT),
                createUser("102", "Jon Doe", createContact(), User.Role.PATIENT),
                createUser("103", "Ed Nygma", createContact(), User.Role.TECHNICIAN)
        );

        static User createUser(String id, String fullName, Contact contact, User.Role role) {
            return User.newBuilder()
                    .setId(id)
                    .setFullName(fullName)
                    .setContact(contact)
                    .setRole(role)
                    .build();
        }

        static Contact createContact() {
            return Contact.newBuilder()
                    .setAddress("ul. Mickiewicza 35, 31-814 Krakow")
                    .setMail("sk@mail.com")
                    .setPhoneNumber("+48-123456789")
                    .build();
        }
    }
}