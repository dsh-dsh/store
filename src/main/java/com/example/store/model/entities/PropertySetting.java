package com.example.store.model.entities;

import com.example.store.model.enums.SettingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "default_property_setting")
public class  PropertySetting {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "setting_type")
    private SettingType settingType;

    @Column(name = "property")
    private int property;

    public static PropertySetting of(SettingType type, User user, int property) {
        PropertySetting setting = new PropertySetting();
        setting.settingType = type;
        setting.user = user;
        setting.property = property;
        return setting;
    }

}
