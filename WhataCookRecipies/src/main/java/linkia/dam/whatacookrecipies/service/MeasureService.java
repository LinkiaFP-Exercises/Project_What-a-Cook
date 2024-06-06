package linkia.dam.whatacookrecipies.service;

import linkia.dam.whatacookrecipies.service.contracts.MeasureDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class MeasureService {

    private final MeasureDao measureDao;

}
