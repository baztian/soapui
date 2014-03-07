/*
 * SoapUI, copyright (C) 2004-2014 smartbear.com
 *
 * SoapUI is free software; you can redistribute it and/or modify it under the
 * terms of version 2.1 of the GNU Lesser General Public License as published by
 * the Free Software Foundation.
 *
 * SoapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details at gnu.org.
 */

package com.eviware.soapui.support.editor.inspectors.auth;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.rest.OAuth2Profile;
import com.eviware.soapui.impl.rest.actions.oauth.GetOAuthAccessTokenAction;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.components.SimpleBindingForm;
import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import static javax.swing.BorderFactory.*;

/**
 *
 */
public class OAuth2AccessTokenForm
{

	public static final String ACCESS_TOKEN_FORM_DIALOG_NAME = "getAccessTokenFormDialog";
	private static final String ACCESS_TOKEN_FORM_DIALOG_TITLE = "Get Access Token";

	public static final int NORMAL_SPACING = 10;
	public static final int GROUP_SPACING = 20;
	public static final String OAUTH_2_FLOW_COMBO_BOX_NAME = "OAuth2Flow";
	public static final Color CARD_BORDER_COLOR = new Color( 121, 121, 121 );
	public static final String CLIENT_IDENTIFICATION = "Client Identification";
	public static final String CLIENT_SECRET = "Client Secret";
	public static final String AUTHORIZATION_URI = "Authorization URI";
	public static final String ACCESS_TOKEN_URI = "Access Token URI";
	public static final String REDIRECT_URI = "Redirect URI";
	public static final String SCOPE = "Scope";

	private OAuth2Profile profile;
	private JDialog accessTokenFormDialog;

	public OAuth2AccessTokenForm( OAuth2Profile profile )
	{
		this.profile = profile;

	}

	public JDialog getComponent()
	{
		createAccessTokenDialog();

		JPanel wrapperPanel = new JPanel( new BorderLayout() );
		wrapperPanel.add( createAccessTokenFormPanel(), BorderLayout.NORTH );
		JLabel accessTokenDocumentationLink = UISupport.createLabelLink( "http://www.soapui.org",
				"How to get an access token from an authorization server" );
		accessTokenDocumentationLink.setBorder( createEmptyBorder( 10, 5, 0, 0 ) );
		wrapperPanel.add( accessTokenDocumentationLink, BorderLayout.SOUTH );

		wrapperPanel.setBorder( createCompoundBorder( createLineBorder( CARD_BORDER_COLOR ),
				createEmptyBorder( 10, 10, 10, 10 ) ) );

		accessTokenFormDialog.getContentPane().add( wrapperPanel );
		return accessTokenFormDialog;
	}

	private JPanel createAccessTokenFormPanel()
	{
		SimpleBindingForm accessTokenForm = new SimpleBindingForm( new PresentationModel<OAuth2Profile>( profile ) );

		JLabel formTitleLabel = new JLabel( "Get Access Token from the authorization server" );
		Font font = formTitleLabel.getFont();
		Font fontBold = new Font( font.getName(), Font.BOLD, font.getSize() );
		formTitleLabel.setFont( fontBold );
		accessTokenForm.addComponent( formTitleLabel );

		accessTokenForm.addSpace( NORMAL_SPACING );

		AbstractValueModel valueModel = accessTokenForm.getPresentationModel().getModel( OAuth2Profile.OAUTH2_FLOW_PROPERTY,
				"getOAuth2Flow", "setOAuth2Flow" );
		ComboBoxModel oauth2FlowsModel = new DefaultComboBoxModel<OAuth2Profile.OAuth2Flow>( OAuth2Profile.OAuth2Flow.values() );
		JComboBox oauth2FlowComboBox = accessTokenForm.appendComboBox( "OAuth2.0 Flow", oauth2FlowsModel, "OAuth2.0 Authorization Flow", valueModel );
		oauth2FlowComboBox.setName( OAUTH_2_FLOW_COMBO_BOX_NAME );

		accessTokenForm.addSpace( GROUP_SPACING );

		accessTokenForm.appendTextField( OAuth2Profile.CLIENT_ID_PROPERTY, CLIENT_IDENTIFICATION, "" );
		final JTextField clientSecretField = accessTokenForm.appendTextField( OAuth2Profile.CLIENT_SECRET_PROPERTY,
				CLIENT_SECRET, "" );
		if( valueModel.getValue() == OAuth2Profile.OAuth2Flow.IMPLICIT_GRANT )
		{
			clientSecretField.setVisible( false );
		}
		oauth2FlowComboBox.addItemListener( new ItemListener()
		{
			@Override
			public void itemStateChanged( ItemEvent e )
			{
				if( e.getStateChange() == ItemEvent.SELECTED )
				{
					clientSecretField.setVisible( e.getItem() != OAuth2Profile.OAuth2Flow.IMPLICIT_GRANT );
					accessTokenFormDialog.pack();
				}
			}
		} );

		accessTokenForm.addSpace( GROUP_SPACING );

		accessTokenForm.appendTextField( OAuth2Profile.AUTHORIZATION_URI_PROPERTY, AUTHORIZATION_URI, "" );
		accessTokenForm.appendTextField( OAuth2Profile.ACCESS_TOKEN_URI_PROPERTY, ACCESS_TOKEN_URI, "" );
		accessTokenForm.appendTextField( OAuth2Profile.REDIRECT_URI_PROPERTY, REDIRECT_URI, "" );

		accessTokenForm.addSpace( GROUP_SPACING );

		accessTokenForm.appendTextField( OAuth2Profile.SCOPE_PROPERTY, SCOPE, "" );

		accessTokenForm.addSpace( NORMAL_SPACING );

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		buttonPanel.add( new JButton( new GetOAuthAccessTokenAction( profile ) ) );
		buttonPanel.add( new JButton( new EditAutomationScriptsAction( profile ) ) );
		accessTokenForm.addComponent( buttonPanel );
		accessTokenForm.appendLabel( OAuth2Profile.ACCESS_TOKEN_STATUS_PROPERTY, "Access token status" );

		return accessTokenForm.getPanel();
	}

	private void createAccessTokenDialog()
	{
		this.accessTokenFormDialog = new JDialog();
		accessTokenFormDialog.setName( ACCESS_TOKEN_FORM_DIALOG_NAME );
		accessTokenFormDialog.setTitle( ACCESS_TOKEN_FORM_DIALOG_TITLE );
		accessTokenFormDialog.setIconImages( SoapUI.getFrameIcons() );
		accessTokenFormDialog.setUndecorated( true );
	}

	private class EditAutomationScriptsAction extends AbstractAction
	{
		private final OAuth2Profile profile;

		public EditAutomationScriptsAction( OAuth2Profile profile )
		{
			putValue( Action.NAME, "Automation..." );
			this.profile = profile;
		}

		@Override
		public void actionPerformed( ActionEvent e )
		{
			if( accessTokenFormDialog != null )
			{
				accessTokenFormDialog.setVisible( false );
				accessTokenFormDialog.dispose();
			}
			UISupport.showDesktopPanel( new OAuth2ScriptsDesktopPanel( profile ) );
		}
	}
}
