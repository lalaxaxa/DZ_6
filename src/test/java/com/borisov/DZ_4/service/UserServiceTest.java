package com.borisov.DZ_4.service;

import com.borisov.DZ_4.dto.UserCreateDTO;
import com.borisov.DZ_4.dto.UserResponseDTO;
import com.borisov.DZ_4.mappers.UserMapper;
import com.borisov.DZ_4.messaging.events.UserCreatedEvent;
import com.borisov.DZ_4.messaging.events.UserDeletedEvent;
import com.borisov.DZ_4.models.User;
import com.borisov.DZ_4.repositories.UserRepository;
import com.borisov.DZ_4.util.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ApplicationEventPublisher publisher;

    @Spy
    private UserMapper userMapper = new UserMapper(new ModelMapper());

    private UserService userService;


    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, userMapper, publisher);
    }

    @Test
    @DisplayName("findAll returns mapped DTO list")
    void findAllShouldReturnDtos() {
        User u1 = new User(1, "Alice", "a@example.com", 20, null);
        User u2 = new User(2, "Bob", "b@example.com", 25, null);

        when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<UserResponseDTO> dtos = userService.findAll();

        assertEquals(2, dtos.size());
        assertEquals("Alice", dtos.get(0).getName());
        assertEquals("b@example.com", dtos.get(1).getEmail());
        verify(userMapper, times(2)).toResponseDTO(any(User.class));
    }

    @Test
    @DisplayName("findById existing id returns DTO")
    void findByIdShouldReturnDto() {
        User u = new User(5, "Carol", "c@example.com", 30, null);
        when(userRepository.findById(5)).thenReturn(Optional.of(u));

        UserResponseDTO dto = userService.findById(5);

        assertEquals("Carol", dto.getName());
        assertEquals(30, dto.getAge());
        verify(userMapper).toResponseDTO(u);
    }

    @Test
    @DisplayName("findById non-existing throws")
    void findByIdNotFoundShouldThrow() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findById(99));
    }

    @Test
    @DisplayName("existsByEmail without excludeId")
    void existsByEmailWithoutExclude() {
        when(userRepository.findByEmail("x@x.com")).thenReturn(Optional.of(new User()));
        assertTrue(userService.existsByEmail("x@x.com", null));
        when(userRepository.findByEmail("nobody")).thenReturn(Optional.empty());
        assertFalse(userService.existsByEmail("nobody", null));
    }

    @Test
    @DisplayName("existsByEmail with excludeId filters out same id")
    void existsByEmailWithExclude() {
        User u = new User(); u.setId(10);
        when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.of(u));
        assertFalse(userService.existsByEmail("a@a.com", 10));
        assertTrue(userService.existsByEmail("a@a.com", 5));
    }

    @Test
    @DisplayName("save should map, set fields and return id")
    void saveShouldMapAndReturnId() {
        UserCreateDTO dto = new UserCreateDTO("Dan", "d@example.com", 40);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(20);
            return u;
        });

        int id = userService.save(dto);

        assertEquals(20, id);
        verify(userMapper).toEntity(dto);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("save should published UserCreatedEvent")
    void savePublishEventAfterUserCreated() {
        UserCreateDTO dto = new UserCreateDTO("Dan", "d@example.com", 40);
        int id = userService.save(dto);

        ArgumentCaptor<UserCreatedEvent> captor = ArgumentCaptor.forClass(UserCreatedEvent.class);
        verify(publisher).publishEvent(captor.capture());
        assertEquals("d@example.com", captor.getValue().getEmail());

    }

    @Test
    @DisplayName("save should throw exception and not published event")
    void saveThrowExceptionAndAndNotPublishedEvent() {
        UserCreateDTO dto = new UserCreateDTO("Dan", "d@example.com", 40);
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB Error"));
        assertThrows(RuntimeException.class, () -> userService.save(dto));
        verify(publisher, never()).publishEvent(any());

    }

    @Test
    @DisplayName("updateById should map, update and return DTO")
    void updateByIdShouldUpdateAndReturnDto() {
        User oldUser = new User(); oldUser.setId(3);
        oldUser.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        when(userRepository.findById(3)).thenReturn(Optional.of(oldUser));

        UserCreateDTO dto = new UserCreateDTO("Eve", "e@example.com", 28);
        doAnswer(invocation -> invocation.getArgument(0)).when(userRepository).save(any(User.class));

        UserResponseDTO result = userService.updateById(3, dto);

        assertEquals(3, result.getId());
        assertEquals("Eve", result.getName());
        verify(userMapper).toEntity(dto);
        verify(userMapper).toResponseDTO(any(User.class));
    }

    @Test
    @DisplayName("deleteById should delete existing user")
    void deleteByIdShouldDelete() {
        User u = new User(); u.setId(7);
        when(userRepository.findById(7)).thenReturn(Optional.of(u));

        assertDoesNotThrow(() -> userService.deleteById(7));
        verify(userRepository).deleteById(7);
    }
    @Test
    @DisplayName("deleteById should published UserDeletedEvent")
    void deleteByIdPublishEventAfterUserDeleted() {
        User u = new User(); u.setId(7); u.setEmail("d@example.com");
        when(userRepository.findById(7)).thenReturn(Optional.of(u));
        userService.deleteById(7);

        ArgumentCaptor<UserDeletedEvent>  captor = ArgumentCaptor.forClass(UserDeletedEvent.class);
        verify(publisher).publishEvent(captor.capture());
        assertEquals("d@example.com", captor.getValue().getEmail());
    }

    @Test
    @DisplayName("deleteById non-existing throws and not published event")
    void deleteByIdNotFoundShouldThrowAndNotPublishedEvent() {
        when(userRepository.findById(4)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deleteById(4));
        verify(publisher, never()).publishEvent(any());
    }
}
