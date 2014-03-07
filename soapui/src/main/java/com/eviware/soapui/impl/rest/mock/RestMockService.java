package com.eviware.soapui.impl.rest.mock;

import com.eviware.soapui.config.RESTMockActionConfig;
import com.eviware.soapui.config.RESTMockServiceConfig;
import com.eviware.soapui.impl.rest.HttpMethod;
import com.eviware.soapui.impl.rest.RestRequest;
import com.eviware.soapui.impl.rest.RestURIParser;
import com.eviware.soapui.impl.rest.support.RestURIParserImpl;
import com.eviware.soapui.impl.support.AbstractMockService;
import com.eviware.soapui.impl.wsdl.mock.WsdlMockRunContext;
import com.eviware.soapui.model.mock.MockDispatcher;
import com.eviware.soapui.model.mock.MockOperation;
import com.eviware.soapui.model.project.Project;

import java.net.MalformedURLException;
import java.util.List;

public class RestMockService extends AbstractMockService<RestMockAction, RestMockResponse, RESTMockServiceConfig>
{

	public final static String ICON_NAME = "/restMockService.gif";

	public RestMockService( Project project, RESTMockServiceConfig config )
	{
		super( config, project, ICON_NAME );

		List<RESTMockActionConfig> restActionConfigList = config.getRestMockActionList();
		for( RESTMockActionConfig restActionConfig : restActionConfigList )
		{
			RestMockAction restMockAction = new RestMockAction( this, restActionConfig );
			addMockOperation( restMockAction );
		}

		if( !getConfig().isSetProperties() )
			getConfig().addNewProperties();

		setPropertiesConfig( config.getProperties() );

	}

	@Override
	public String getIconName()
	{
		return ICON_NAME;
	}

	@Override
	public MockDispatcher createDispatcher( WsdlMockRunContext mockContext )
	{
		return new RestMockDispatcher( this, mockContext );
	}

	public RestMockAction addNewMockAction( RestRequest restRequest )
	{

		String mockActionName = restRequest.getResource().getName() + " " + restRequest.getPath();
		RESTMockActionConfig config = getConfig().addNewRestMockAction();
		config.setName( mockActionName );
		config.setResourcePath( restRequest.getPath() );
		config.setMethod( restRequest.getMethod().name() );
		RestMockAction restMockAction = new RestMockAction( this, config, restRequest );

		addMockOperation( restMockAction );
		fireMockOperationAdded( restMockAction );

		return restMockAction;
	}

	public MockOperation findOrCreateNewOperation( RestRequest restRequest )
	{
		MockOperation matchedOperation = findMatchingOperation( restRequest.getPath(), restRequest.getMethod() );

		if( matchedOperation == null)
		{
			matchedOperation = addNewMockAction( restRequest );
		}
		return matchedOperation;
	}

	protected MockOperation findMatchingOperation( String pathToFind, HttpMethod verbToFind )
	{
		for( MockOperation operation : getMockOperationList() )
		{
			String operationPath = ( ( RestMockAction )operation ).getResourcePath();
			HttpMethod operationVerb = ( ( RestMockAction )operation ).getMethod();

			boolean matchesPath = operationPath.equals( pathToFind );
			boolean matchesVerb = verbToFind == operationVerb;

			if( matchesPath && matchesVerb )
			{
				return operation;
			}
		}

		return null;
	}

	public boolean canIAddAMockOperation( RestMockAction mockOperation )
	{
		return this.getConfig().getRestMockActionList().contains( mockOperation.getConfig() );
	}
}
