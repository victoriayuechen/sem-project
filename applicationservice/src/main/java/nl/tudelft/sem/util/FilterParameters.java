package nl.tudelft.sem.util;

import java.util.Objects;

public class FilterParameters {
    private transient String minGrade;
    private transient String minRating;
    private transient String minAvgRating;
    private transient String minReqTa;

    /**
     * Constructor for the filter parameters.
     *
     * @param minGrade      The minimum grade.
     * @param minRating     The minimum rating.
     * @param minAvgRating  The minimum average rating.
     * @param minReqTa      The minimum amount of times a TA has been a TA before.
     */
    public FilterParameters(String minGrade, String minRating,
                            String minAvgRating, String minReqTa) {
        this.minGrade = minGrade;
        this.minRating = minRating;
        this.minAvgRating = minAvgRating;
        this.minReqTa = minReqTa;
    }

    public String getMinGrade() {
        return minGrade;
    }

    public String getMinRating() {
        return minRating;
    }

    public String getMinAvgRating() {
        return minAvgRating;
    }

    public String getMinReqTa() {
        return minReqTa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilterParameters)) {
            return false;
        }

        FilterParameters that = (FilterParameters) o;

        if (!Objects.equals(minGrade, that.minGrade)) {
            return false;
        }
        if (!Objects.equals(minRating, that.minRating)) {
            return false;
        }
        if (!Objects.equals(minAvgRating, that.minAvgRating)) {
            return false;
        }
        return Objects.equals(minReqTa, that.minReqTa);
    }

    @Override
    public int hashCode() {
        int result = minGrade != null ? minGrade.hashCode() : 0;
        result = 31 * result + (minRating != null ? minRating.hashCode() : 0);
        result = 31 * result + (minAvgRating != null ? minAvgRating.hashCode() : 0);
        result = 31 * result + (minReqTa != null ? minReqTa.hashCode() : 0);
        return result;
    }
}
