package ru.sicampus.bootcamp2026.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sicampus.bootcamp2026.dto.CreateMeetingDto;
import ru.sicampus.bootcamp2026.dto.MeetingsDto;
import ru.sicampus.bootcamp2026.service.MeetingAttendeesService;
import ru.sicampus.bootcamp2026.service.MeetingService;

import java.time.LocalDate;
import java.util.List;


@RequestMapping("/meetings")
@RequiredArgsConstructor
@RestController
public class MeetingController {

    private final MeetingService meetingService;
    private final MeetingAttendeesService meetingAttendeesService;

    @GetMapping("/{id}")
    public ResponseEntity<MeetingsDto> getMeeting(@PathVariable("id") Long id) {
        return meetingService.getMeeting(id)
                .map(user -> ResponseEntity.ok(user))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createMeeting(
            @RequestBody CreateMeetingDto request
            ) {
        meetingService.createMeeting(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @GetMapping("/user/{userId}")
    public List<MeetingsDto> getMyMeetingsByDay(
            @PathVariable Long userId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return meetingService.getMyMeetingsByDay(userId, date);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMeeting (@PathVariable("id") Long id) {
        meetingService.deleteMeeting(id);
        return ResponseEntity.noContent().build();
    }

}
