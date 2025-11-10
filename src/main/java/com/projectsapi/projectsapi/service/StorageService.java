package com.projectsapi.projectsapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.projectsapi.projectsapi.model.Task;
import com.projectsapi.projectsapi.repository.TaskRepository;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.Map;
import java.util.Optional;

@Service
public class StorageService {

    @Value("${supabase.storage.endpoint}")
    private String storageEndpoint;

    @Value("${supabase.storage.access.key}")
    private String accessKey;

    @Value("${supabase.storage.secret.key}")
    private String secretKey;

    @Value("${supabase.storage.region}")
    private String region;

    @Autowired
    private TaskRepository taskRepository;

    public ResponseEntity<?> uploadAndLinkFileToTask(Integer taskId, MultipartFile file) {
        try {
            String bucket = "project-files";
            String key = "tasks/" + taskId + "/" + file.getOriginalFilename();

            // Configurar credenciales AWS
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
            
            // Crear cliente S3 para Supabase Storage
            S3Client s3Client = S3Client.builder()
                    .endpointOverride(java.net.URI.create(storageEndpoint))
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                    .forcePathStyle(true) // Importante para Supabase Storage
                    .build();

            // Verificar si el bucket existe, si no, crearlo
            try {
                s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
            } catch (NoSuchBucketException e) {
                // Crear el bucket si no existe
                CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                        .bucket(bucket)
                        .build();
                s3Client.createBucket(createBucketRequest);
            }

            // Crear request para subir el archivo
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            // Subir el archivo
            PutObjectResponse response = s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            if (response.sdkHttpResponse().statusCode() != 200) {
                return ResponseEntity.status(500).body("Error al subir archivo a Supabase Storage");
            }

            // Generar URL pública del archivo
            String publicFileUrl = storageEndpoint + "/" + bucket + "/" + key;

            // Actualizar la tarea con la URL del archivo
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                task.setFileURL(publicFileUrl);
                taskRepository.save(task);
            } else {
                return ResponseEntity.status(404).body("Tarea no encontrada");
            }

            return ResponseEntity.ok(Map.of(
                    "taskId", taskId,
                    "fileName", file.getOriginalFilename(),
                    "url", publicFileUrl
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
