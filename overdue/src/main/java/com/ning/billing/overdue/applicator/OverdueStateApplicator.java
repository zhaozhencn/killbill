/*
 * Copyright 2010-2011 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.billing.overdue.applicator;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.ning.billing.ErrorCode;
import com.ning.billing.overdue.OverdueAccessApi;
import com.ning.billing.overdue.config.api.OverdueError;
import com.ning.billing.overdue.config.api.OverdueState;
import com.ning.billing.overdue.config.api.Overdueable;
import com.ning.billing.util.clock.Clock;

public class OverdueStateApplicator<T extends Overdueable>{

    private final OverdueAccessApi accessApi;


    @Inject
    public OverdueStateApplicator(OverdueAccessApi accessApi) {
        this.accessApi = accessApi;
    }

    public void apply(T overdueable, OverdueState<T> previousOverdueState, OverdueState<T> nextOverdueState, DateTime timeOfNextCheck) throws OverdueError {
        if(previousOverdueState.getName().equals(nextOverdueState.getName())) {
            return; // nothing to do
        }
        
        storeNewState(overdueable, nextOverdueState);
  
        if(timeOfNextCheck != null && !nextOverdueState.isClearState()) {
            createFutureNotification(overdueable, timeOfNextCheck);
        }

        if(nextOverdueState.isClearState()) {
            clear(overdueable);
        }
        
        //If new state is clear state reset next events and override table
        throw new NotImplementedException();
    }


    protected void storeNewState(T overdueable, OverdueState<T> nextOverdueState) throws OverdueError {
        try {
            accessApi.setOverrideState(overdueable, nextOverdueState, Overdueable.Type.get(overdueable));
        } catch (Exception e) {
            throw new OverdueError(e, ErrorCode.OVERDUE_CAT_ERROR_ENCOUNTERED, overdueable.getId(), overdueable.getClass().getName());
        }
    }

    protected void createFutureNotification(T overdueable,
            DateTime timeOfNextCheck) {
        // TODO Auto-generated method stub
        
    }


    
    protected void clear(T overdueable) {
        // Clear future notification checks
        // Clear any overrides
        
    }

}
