package org.openlca.app.tools.mapping.model;

import java.util.Objects;

import org.openlca.core.model.Flow;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.FlowPropertyFactor;
import org.openlca.core.model.Unit;
import org.openlca.core.model.descriptors.BaseDescriptor;
import org.openlca.core.model.descriptors.Descriptors;
import org.openlca.core.model.descriptors.FlowDescriptor;

/**
 * A FlowRef describes a reference to a (source or target) flow in a mapping
 * entry.
 */
public class FlowRef {

	/**
	 * The reference to the flow data set.
	 */
	public FlowDescriptor flow;

	public String categoryPath;

	/**
	 * An optional reference to a property (= quantity) of the flow. When this is
	 * missing, the reference flow property of the flow is taken by default.
	 */
	public BaseDescriptor flowProperty;

	/**
	 * Also, the unit reference is optional; the reference unit of the unit group of
	 * the flow property is taken by default.
	 */
	public BaseDescriptor unit;

	/**
	 * Sync this flow references with the given flow. First it tests that the
	 * definition of the flow reference can be fulfilled with the given flow (i.e.
	 * the ref-IDs match and the flow property and unit is defined for that flow).
	 * If this is not the case it returns {@code false}. Otherwise, it will mutate
	 * this reference to have the respective database IDs of the given flow and sets
	 * the property and unit to the respective defaults if they are missing.
	 */
	public boolean syncWith(Flow flow) {
		// check the flow
		if (flow == null || this.flow == null)
			return false;
		if (!Objects.equals(flow.refId, this.flow.refId))
			return false;

		// check the flow property
		FlowProperty prop = null;
		if (flowProperty == null) {
			prop = flow.referenceFlowProperty;
		} else {
			for (FlowPropertyFactor f : flow.flowPropertyFactors) {
				if (f.flowProperty == null)
					continue;
				if (Objects.equals(flowProperty.refId, f.flowProperty.refId)) {
					prop = f.flowProperty;
					break;
				}
			}
		}
		if (prop == null || prop.unitGroup == null)
			return false;

		// check the unit
		Unit u = null;
		if (unit == null) {
			u = prop.unitGroup.referenceUnit;
		} else {
			for (Unit ui : prop.unitGroup.units) {
				if (Objects.equals(unit.refId, ui.refId)) {
					u = ui;
					break;
				}
			}
		}
		if (u == null)
			return false;

		// sync the reference data
		if (flowProperty == null) {
			flowProperty = Descriptors.toDescriptor(prop);
		}
		if (unit == null) {
			unit = Descriptors.toDescriptor(u);
		}
		this.flow.id = flow.id;
		flowProperty.id = prop.id;
		unit.id = u.id;
		return true;
	}

}
