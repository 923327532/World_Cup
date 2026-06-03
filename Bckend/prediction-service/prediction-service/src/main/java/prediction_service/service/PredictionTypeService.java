package prediction_service.service;

import prediction_service.dto.PredictionTypeDTO;
import prediction_service.repository.PredictionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictionTypeService {

    private final PredictionTypeRepository predictionTypeRepository;

    public List<PredictionTypeDTO> getAllTypes() {
        return predictionTypeRepository.findAll().stream()
            .map(type -> new PredictionTypeDTO(type.getId(), type.getCode(), type.getName(), type.getPoints()))
            .toList();
    }
}