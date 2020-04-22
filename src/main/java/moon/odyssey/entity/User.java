package moon.odyssey.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import moon.odyssey.code.TFCode;
import moon.odyssey.converter.TFCodeConverter;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = {"userId"})
public class User implements Serializable {

    @Id
    private String userId;

    private String password;

    @Convert(converter = TFCodeConverter.class)
    @Column(columnDefinition = "char")
    private TFCode isVerified;
}
