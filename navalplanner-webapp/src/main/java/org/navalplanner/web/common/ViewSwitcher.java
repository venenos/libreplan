/*
 * This file is part of ###PROJECT_NAME###
 *
 * Copyright (C) 2009 Fundación para o Fomento da Calidade Industrial e
 *                    Desenvolvemento Tecnolóxico de Galicia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.navalplanner.web.common;

import static org.navalplanner.web.I18nHelper._;

import java.util.HashMap;
import java.util.Map;

import org.navalplanner.web.planner.allocation.AdvancedAllocationController;
import org.navalplanner.web.planner.allocation.AllocationResult;
import org.navalplanner.web.resourceload.ResourceLoadController;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.ganttz.resourceload.ResourcesLoadPanel.IToolbarCommand;
import org.zkoss.zk.ui.util.Composer;

/**
 * @author Óscar González Fernández <ogonzalez@igalia.com>
 *
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ViewSwitcher implements Composer {

    private org.zkoss.zk.ui.Component parent;

    private IChildrenSnapshot planningOrder;

    private boolean isInPlanningOrder = false;

    @Override
    public void doAfterCompose(org.zkoss.zk.ui.Component comp) throws Exception {
        this.parent = comp;
        isInPlanningOrder = true;
    }

    public void goToAdvancedAllocation(AllocationResult allocationResult) {
        planningOrder = ComponentsReplacer.replaceAllChildren(parent,
                "advance_allocation.zul",
                createArgsForAdvancedAllocation(allocationResult));
        isInPlanningOrder = false;
    }

    private Map<String, Object> createArgsForAdvancedAllocation(
            AllocationResult allocationResult) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("advancedAllocationController",
                new AdvancedAllocationController(this, allocationResult));
        return result;
    }

    public void goToPlanningOrderView() {
        if (isInPlanningOrder) {
            return;
        }
        planningOrder.restore();
        isInPlanningOrder = true;
    }

    public void goToResourceLoad(ResourceLoadController resourceLoadController) {
        addCommands(resourceLoadController);
        planningOrder = ComponentsReplacer.replaceAllChildren(parent,
                "../resourceload/resourceloadfororder.zul",
                argsForResourceLoad(resourceLoadController));
        isInPlanningOrder = false;
    }

    private void addCommands(ResourceLoadController resourceLoadController) {
        resourceLoadController.add(new IToolbarCommand() {

            @Override
            public void doAction() {
                goToPlanningOrderView();
            }

            @Override
            public String getLabel() {
                return _("Back to Order Planning View");
            }
        });

    }

    private Map<String, Object> argsForResourceLoad(
            ResourceLoadController resourceLoadController) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("resourceLoadController", resourceLoadController);
        return result;
    }

}
