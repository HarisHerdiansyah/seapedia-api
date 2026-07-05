package com.github.harisherdiansyah.seapediaapi.features.delivery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    public List<DeliveryMethodResponseDTO> populateDeliveryMethods() {
        List<DeliveryEntity> deliveryEntities = deliveryRepository.findAll();
        return deliveryEntities.stream()
                .map(deliveryEntity -> new DeliveryMethodResponseDTO(
                        deliveryEntity.getId(),
                        deliveryEntity.getDeliveryMethod(),
                        deliveryEntity.getPrice()
                ))
                .toList();
    }
}
