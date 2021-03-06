/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2015 by Pentaho : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.trans.steps.combinationlookup;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseInterfaceExtended;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepPartitioningMeta;

import java.sql.Connection;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

public class CombinationLookupTest {
  private DatabaseMeta databaseMeta;
  private DatabaseInterfaceExtended databaseInterface;

  private StepMeta stepMeta;

  private CombinationLookup combinationLookup, combinationLookupSpy;
  private CombinationLookupMeta combinationLookupMeta;
  private CombinationLookupData combinationLookupData;

  @Before
  public void setup() {
    databaseMeta = mock( DatabaseMeta.class );
    databaseInterface = mock( DatabaseInterfaceExtended.class );
    doReturn( databaseInterface ).when( databaseMeta ).getDatabaseInterface();
    doReturn( "" ).when( databaseMeta ).quoteField( anyString() );

    combinationLookupMeta = mock( CombinationLookupMeta.class );
    doReturn( databaseMeta ).when( combinationLookupMeta ).getDatabaseMeta();
    doReturn( "sasas" ).when( combinationLookupMeta ).getTechnicalKeyField();
    doReturn( new String[] { } ).when( combinationLookupMeta ).getKeyLookup();

    stepMeta = mock( StepMeta.class );
    doReturn( "step" ).when( stepMeta ).getName();
    doReturn( mock( StepPartitioningMeta.class ) ).when( stepMeta ).getTargetStepPartitioningMeta();
    doReturn( combinationLookupMeta ).when( stepMeta ).getStepMetaInterface();

    Database db = mock( Database.class );
    doReturn( mock( Connection.class ) ).when( db ).getConnection();

    combinationLookupData = mock( CombinationLookupData.class );
    combinationLookupData.db = db;
    combinationLookupData.keynrs = new int[] { };

    TransMeta transMeta = mock( TransMeta.class );
    doReturn( stepMeta ).when( transMeta ).findStep( anyString() );

    combinationLookup = new CombinationLookup( stepMeta, combinationLookupData, 1, transMeta, mock( Trans.class ) );
    combinationLookupSpy = spy( combinationLookup );
    doReturn( stepMeta ).when( combinationLookupSpy ).getStepMeta();
    doReturn( false ).when( combinationLookupSpy ).isRowLevel();
    doNothing().when( combinationLookupSpy ).logDetailed( anyString() );
  }

  @Test
  public void testCombiInsert() throws Exception {
    combinationLookupSpy.combiInsert( any( RowMetaInterface.class ), any( Object[].class ), anyLong(), anyLong() );
    verify( databaseInterface, times( 2 ) ).supportsAutoGeneratedKeys();
  }
}
