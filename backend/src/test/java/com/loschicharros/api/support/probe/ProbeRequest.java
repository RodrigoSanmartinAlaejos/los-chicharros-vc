package com.loschicharros.api.support.probe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProbeRequest(
    @NotBlank @Size(max = 60) String name, @NotBlank @Size(max = 5) String code) {}
