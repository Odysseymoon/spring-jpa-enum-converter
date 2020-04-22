package moon.odyssey.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import moon.odyssey.code.TFCode;
import moon.odyssey.constraint.EnumValue;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserInfo {

    @NotEmpty
    @Length(min = 4, max = 30)
    private String userId;

    @NotEmpty
    @Length(min = 8, max = 50)
    private String password;

    @EnumValue(enumClazz = TFCode.class, values = {"TRUE", "FALSE"}, message = "Invalid Code")
    private TFCode isVerified;
}
