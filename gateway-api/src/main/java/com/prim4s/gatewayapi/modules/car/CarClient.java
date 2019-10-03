package com.prim4s.gatewayapi.modules.car;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.hateoas.Resources;

@FeignClient("car-api")
public interface CarClient {

    @GetMapping("/cars")
    @CrossOrigin
    Resources<Car> readCars();
}
