package org.chat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupDto {
    private String id;
    private String name;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String[] creators;
}
