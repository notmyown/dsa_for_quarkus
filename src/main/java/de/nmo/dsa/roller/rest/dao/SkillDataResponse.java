package de.nmo.dsa.roller.rest.dao;

import de.nmo.dsa.roller.entity.Skill;
import de.nmo.dsa.roller.entity.SkillToUser;

public class SkillDataResponse {

    private final long id;
    private final String name;
    private final String category;
    private final String attributes;
    private final long value;

    public SkillDataResponse(SkillToUser su, Skill s) {
        id = su.getSkill();
        name = s.getName();
        category = s.getCategory();
        attributes = s.getAttributes();
        value = su.getValue();
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getAttributes() {
        return attributes;
    }

    public long getId() {
        return id;
    }

    public long getValue() {
        return value;
    }
}
