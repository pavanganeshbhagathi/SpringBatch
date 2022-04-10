package com.example.batch.batch.controller;

import com.example.batch.batch.Entity.Profile;
import com.example.batch.batch.Entity.User;
import com.example.batch.batch.repository.ProfileRepository;
import com.example.batch.batch.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/batch")
@AllArgsConstructor
public class BatchController {

    private UserRepository userRepository;
    private ProfileRepository profileRepository;

    private JobLauncher jobLauncher;

    private Job job;
    @GetMapping(path = "/Users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> all = userRepository.findAll();
        return new ResponseEntity<List<User>>(all, HttpStatus.OK);
    }

    @GetMapping(path = "/profiles")
    public ResponseEntity<List<Profile>> getProfiles() {
        List<Profile> all = profileRepository.findAll();
        return new ResponseEntity<List<Profile>>(all, HttpStatus.OK);
    }


    @PostMapping(path = "/createUser")
    public ResponseEntity<User> saveUser(@Valid @RequestBody User user) {
        return new ResponseEntity<User>(userRepository.save(user), HttpStatus.CREATED);
    }

    @PostMapping(path = "/createProfile")
    public ResponseEntity<Profile> saveProfile(@Valid @RequestBody Profile profile) {
        return new ResponseEntity<Profile>(profileRepository.save(profile), HttpStatus.CREATED);
    }
    @PostMapping(path = "/createUsers")
    public ResponseEntity<List<User>> saveUsers(@Valid @RequestBody List<User> user) {
        return new ResponseEntity<List<User>>(userRepository.saveAll(user), HttpStatus.CREATED);
    }
    @PostMapping(path = "/start")
    public void startBatch() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis()).toJobParameters();
        try {
            jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException
                | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {

            e.printStackTrace();
        }
    }

}
