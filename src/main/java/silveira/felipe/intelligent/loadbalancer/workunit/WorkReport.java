/**
 * MIT License
 *
 * Copyright (c) 2017.  Felipe Silveira
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package silveira.felipe.intelligent.loadbalancer.workunit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This class encapsulates work report information.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkReport {

    /**
     * UUID of the workUnionId.
     */
    @JsonProperty(required = true)
    private String workUnionId;

    /**
     * Work Accepted boolean.
     */
    @JsonProperty(required = true)
    private Boolean workAccepted;

    /**
     * WorkReport constructor.
     *
     * @param workUnionId UUID of the workUnionId.
     * @param workAccepted work accepted boolean.
     */
    public WorkReport(String workUnionId, Boolean workAccepted) {
        this.workUnionId = workUnionId;
        this.workAccepted = workAccepted;
    }

    /**
     * WorkReport default constructor.
     */
    public WorkReport() {
        //Used by retrofit.
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.workUnionId)
                .append(this.workAccepted)
                .toHashCode();
    }

    /**
     * WorkReport String representation.
     *
     * @return a String with workReport information.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("workUnionId", workUnionId)
                .append("workAccepted", workAccepted)
                .toString();
    }

    public String getWorkUnionId() {
        return workUnionId;
    }

    public void setWorkUnionId(String workUnionId) {
        this.workUnionId = workUnionId;
    }

    public Boolean getWorkAccepted() {
        return workAccepted;
    }

    public void setWorkAccepted(Boolean workAccepted) {
        this.workAccepted = workAccepted;
    }
}
