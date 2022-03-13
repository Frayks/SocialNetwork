package andrew.project.socialNetwork.backend.services;

import andrew.project.socialNetwork.backend.api.dtos.SaveImageRequestDto;
import andrew.project.socialNetwork.backend.api.dtos.SaveImageResponseDto;
import andrew.project.socialNetwork.backend.api.mappers.Mapper;
import andrew.project.socialNetwork.backend.api.properties.ImageStorageProperties;
import andrew.project.socialNetwork.backend.api.services.ImageStorageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.xml.ws.http.HTTPException;
import java.net.URI;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    private static final Logger LOGGER = LogManager.getLogger(ImageStorageServiceImpl.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private ImageStorageProperties imageStorageProperties;


    @Override
    public SaveImageResponseDto saveImage(MultipartFile file) throws Exception {
        SaveImageRequestDto requestDto = Mapper.mapToSaveImageResponseDto(file);
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(imageStorageProperties.getUsername(), imageStorageProperties.getPassword());
        HttpEntity<SaveImageRequestDto> request = new HttpEntity<>(requestDto, headers);
        URI uri = UriComponentsBuilder
                .fromUriString(imageStorageProperties.getUrl())
                .path(imageStorageProperties.getSaveImageEndpoint())
                .build().toUri();
        ResponseEntity<SaveImageResponseDto> responseDto = restTemplate.exchange(uri, HttpMethod.POST, request, SaveImageResponseDto.class);
        if (responseDto.getStatusCode().equals(HttpStatus.OK)) {
            return responseDto.getBody();
        }
        throw new HTTPException(responseDto.getStatusCode().value());
    }

    @Override
    public void deleteImage(String imageName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(imageStorageProperties.getUsername(), imageStorageProperties.getPassword());
        HttpEntity<?> request = new HttpEntity<>(headers);
        URI uri = UriComponentsBuilder
                .fromUriString(imageStorageProperties.getUrl())
                .path(imageStorageProperties.getDeleteImageEndpoint())
                .queryParam("name", imageName).build().toUri();
        ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.GET, request, Void.class);
        if (!response.getStatusCode().equals(HttpStatus.OK) && !response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            throw new HTTPException(response.getStatusCode().value());
        }
    }

    @Autowired
    public void setImageStorageProperties(ImageStorageProperties imageStorageProperties) {
        this.imageStorageProperties = imageStorageProperties;
    }
}
