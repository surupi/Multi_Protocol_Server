package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class GreetingCommand {
    private String name;
    @Builder.Default
    private List<String> arguments = new ArrayList<>();
}
