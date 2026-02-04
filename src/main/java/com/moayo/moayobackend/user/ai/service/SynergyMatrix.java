package com.moayo.moayobackend.user.ai.service;

import com.moayo.moayobackend.user.ai.entity.JobTag;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

@Component
public class SynergyMatrix {

    public Set<JobTag> complementsOf(Set<JobTag> mine) {
        EnumSet<JobTag> comps = EnumSet.noneOf(JobTag.class);

        if (mine.contains(JobTag.PLANNING)) {
            comps.add(JobTag.DEVELOPMENT);
            comps.add(JobTag.DESIGN);
            comps.add(JobTag.MARKETING);
        }
        if (mine.contains(JobTag.DEVELOPMENT)) {
            comps.add(JobTag.PLANNING);
            comps.add(JobTag.DESIGN);
        }
        if (mine.contains(JobTag.DESIGN)) {
            comps.add(JobTag.PLANNING);
            comps.add(JobTag.DEVELOPMENT);
            comps.add(JobTag.MARKETING);
        }
        if (mine.contains(JobTag.MARKETING)) {
            comps.add(JobTag.PLANNING);
            comps.add(JobTag.DESIGN);
        }
        if (mine.contains(JobTag.STARTUP)) {
            comps.add(JobTag.PLANNING);
            comps.add(JobTag.DEVELOPMENT);
            comps.add(JobTag.DESIGN);
            comps.add(JobTag.MARKETING);
        }

        if (comps.isEmpty()) comps.add(JobTag.ETC);
        return comps;
    }

    public double synergyScore(Set<JobTag> mine, Set<JobTag> candidate) {
        Set<JobTag> comps = complementsOf(mine);

        int hit = 0;
        for (JobTag t : candidate) {
            if (comps.contains(t)) hit++;
        }
        return Math.min(1.0, hit / (double) Math.max(1, comps.size()));
    }
}
