package ru.sicampus.bootcamp2026.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sicampus.bootcamp2026.dto.CreateMeetingDto;
import ru.sicampus.bootcamp2026.dto.MeetingsDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface MeetingService {

    Optional<MeetingsDto> getMeeting(Long id);

    void createMeeting(CreateMeetingDto request);

    List<MeetingsDto> getMyMeetingsByDay(Long userId, LocalDate date);

    void deleteMeeting(long id);
}