package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.Recipe;

public interface JobService {

    void runJobFromRecipe(Recipe recipe);
}
