package com.projectsapi.projectsapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.projectsapi.projectsapi.model.ProjectStatus;
import com.projectsapi.projectsapi.repository.ProjectStatusRepository;
import java.util.List;

@RestController
@RequestMapping("/project-status")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://traducianistic-immorally-harmony.ngrok-free.dev",
        "https://nonblunderingly-micropaleontological-mabelle.ngrok-free.dev",
        "https://preceremonial-forgivingly-natisha.ngrok-free.dev"
})
public class ProjectStatusController {

    @Autowired
    private ProjectStatusRepository projectStatusRepository;

    @GetMapping
    public ResponseEntity<List<ProjectStatus>> getAllStatuses() {
        List<ProjectStatus> statuses = projectStatusRepository.findAll();
        return ResponseEntity.ok(statuses);
    }
}
