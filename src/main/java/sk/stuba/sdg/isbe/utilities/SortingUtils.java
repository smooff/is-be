package sk.stuba.sdg.isbe.utilities;

import sk.stuba.sdg.isbe.domain.enums.SortDirectionEnum;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;

public final class SortingUtils {

    public static SortDirectionEnum getSortDirection(String sortDirection) {
        try {
            return Enum.valueOf(SortDirectionEnum.class, sortDirection.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundCustomException("Sort direction: '" + sortDirection + "' does not exist! Possible options are ASC or DESC!");
        }
    }
}
