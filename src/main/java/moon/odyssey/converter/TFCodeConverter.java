package moon.odyssey.converter;

import javax.persistence.Converter;

import moon.odyssey.code.TFCode;

@Converter(autoApply = true)
public class TFCodeConverter extends AbstractBaseEnumConverter<TFCode, String> {

    @Override
    protected TFCode[] getValueList() {
        return TFCode.values();
    }
}
