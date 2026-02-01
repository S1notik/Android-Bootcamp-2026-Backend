package ru.sicampus.bootcamp2026.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.sicampus.bootcamp2026.dto.CreateMeetingDto;
import ru.sicampus.bootcamp2026.dto.MeetingsDto;
import ru.sicampus.bootcamp2026.model.entity.MeetingAttendees;
import ru.sicampus.bootcamp2026.model.entity.MeetingAttendeesId;
import ru.sicampus.bootcamp2026.model.entity.Meetings;
import ru.sicampus.bootcamp2026.model.entity.Users;
import ru.sicampus.bootcamp2026.model.enums.UserStatus;
import ru.sicampus.bootcamp2026.repository.MeetingAttendeesRepository;
import ru.sicampus.bootcamp2026.repository.MeetingsRepository;
import ru.sicampus.bootcamp2026.repository.UserRepository;
import ru.sicampus.bootcamp2026.service.MeetingService;
import ru.sicampus.bootcamp2026.util.MeetingMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class MeetingServiceImpl implements MeetingService {

    private final MeetingsRepository meetingRepository;

    private final UserRepository userRepository;

    private final MeetingAttendeesRepository meetingAttendeesRepository;

    @Override
    public Optional<MeetingsDto> getMeeting(Long id) {
        try {
            return meetingRepository.findById(id).map(MeetingMapper::toDto);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Transactional
    @Override
    public void createMeeting(CreateMeetingDto request) {

        if (request.getCreatorId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "creatorId must not be null"
            );
        }

        if (request.getAttendeeIds() == null || request.getAttendeeIds().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "attendeeIds must not be empty"
            );
        }

        if (request.getAttendeeIds().contains(null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "attendeeIds contains null value"
            );
        }

        Users creator = userRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Creator not found"));

        Meetings meeting = new Meetings();
        meeting.setCreator(creator);
        meeting.setTitle(request.getTitle());
        meeting.setDescription(request.getDescription());
        meeting.setStartTime(request.getStartTime());
        meeting.setEndTime(request.getEndTime());
        meeting.setLocation(request.getLocation());
        meeting.setStatus(request.getStatus());

        meeting = meetingRepository.saveAndFlush(meeting);
        for (Long userId : request.getAttendeeIds()) {

            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "User not found"));

            MeetingAttendees attendee = new MeetingAttendees();
            attendee.setId(new MeetingAttendeesId(
                    meeting.getId(),
                    user.getId()
            ));
            attendee.setMeeting(meeting);
            attendee.setUser(user);
            attendee.setStatus(UserStatus.PENDING);

            meetingAttendeesRepository.save(attendee);
        }

    }

    @Override
    public List<MeetingsDto> getMyMeetingsByDay(Long userId, LocalDate date) {
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();

        return meetingRepository
                .findMyMeetingsByDay(userId, from, to)
                .stream()
                .map(MeetingMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public void deleteMeeting(long id) {
        Meetings meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Meeting not found"));

        // Удалить участников
        meetingAttendeesRepository.deleteAll(meeting.getAttendees());
        meetingRepository.delete(meeting);
    }

}
