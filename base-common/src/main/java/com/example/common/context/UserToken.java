package com.example.common.context;

import com.example.common.util.JsonObjectConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserToken implements Principal, Serializable {
    public static final String TENANT_ID = "tid";
    public static final String CODE_STR = "code";
    public static final String VERSION_STR = "version";
    public static final String PROPS = "props";
    private static final long serialVersionUID = 7086389294033411035L;

    @Getter
    private String id;

    @Getter
    private String name;

    @Getter
    private LocalDateTime expireAt;

    @Getter
    private LocalDateTime signAt;

    @Getter
    private Map<String, Object> properties;

    @Getter
    private Integer version;

    @Getter
    private Long code;

    public String toJson() {
        try {
            return JsonObjectConverter.objectToJson(this);
        } catch (JsonProcessingException e) {
            log.warn("Unable to convert to json String for user {}", getId());
            return "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserToken userToken = (UserToken) o;
        return Objects.equals(id, userToken.id) &&
                Objects.equals(name, userToken.name) &&
                Objects.equals(version, userToken.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, version);
    }
}
