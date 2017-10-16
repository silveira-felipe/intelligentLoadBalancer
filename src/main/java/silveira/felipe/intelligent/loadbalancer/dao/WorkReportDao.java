package silveira.felipe.intelligent.loadbalancer.dao;

import silveira.felipe.intelligent.loadbalancer.workunit.WorkReport;

import javax.annotation.Nonnull;

/**
 * WorkReport DAO Interface.
 */
public interface WorkReportDao {

    /**
     * Stores a new workReport in the DB.
     *
     * @param workReport the workReport information to be stored
     */
    void addRequestInformation(@Nonnull WorkReport workReport);

}
