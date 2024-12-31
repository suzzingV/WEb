package dsm.webauthn.webauthntest.domain.webauthn.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "pub_key_cred_param_TB")
public class PubKeyCredParam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer alg;

    private String type;

    @Builder
    private PubKeyCredParam(Integer alg, String type) {
        this.alg = alg;
        this.type = type;
    }
}
