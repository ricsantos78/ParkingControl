package com.api.parkingcontrol.models;

import com.api.parkingcontrol.enums.RoleName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
@Table(name = "TB_ROLE")
public class RoleModel implements GrantedAuthority,Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,unique = true)
    private RoleName name;


    @Override
    public String getAuthority() {
        return name.toString();
    }
}
