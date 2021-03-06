package com.nahmens.rhcimax.controlador;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nahmens.rhcimax.R;
import com.nahmens.rhcimax.database.DataBaseHelper;
import com.nahmens.rhcimax.database.modelo.Usuario;
import com.nahmens.rhcimax.database.sqliteDAO.UsuarioSqliteDao;
import com.nahmens.rhcimax.seguridad.Owasp;
import com.nahmens.rhcimax.utils.SesionUsuario;
import com.nahmens.rhcimax.utils.SincronizacionAsyncTask;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.login, menu); return true; }
	 */

	/*
	 * Funcion que verifica si el usuario es v�lido o no.
	 */
	public void onClickValidar(View v) {
		EditText etLogin = (EditText) findViewById(R.id.editTextLogin);
		EditText etPassword = (EditText) findViewById(R.id.editTextPassword);
		Owasp mOwasp = new Owasp();
		boolean passValido = false;

		String login = etLogin.getText().toString();
		String password = etPassword.getText().toString();

		UsuarioSqliteDao usuSqlDao = new UsuarioSqliteDao();
		Usuario usu = usuSqlDao.buscarUsuarioByLogin(this, login);


		if (usu != null) {

			try {
				passValido = mOwasp.authenticate(login, password, usu.getSalt(), usu.getPassword());
				
				Log.e("Login","encodedPass: "+usu.getPassword()+", rawPass: "+ password+",  SALT: "+usu.getSalt() + " isValiod: " + passValido);

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),
						"Ha ocurrido un error inesperado. Int�ntelo de nuevo.", Toast.LENGTH_LONG)
						.show();
				
			} catch (SQLException e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(),
						"Ha ocurrido un error inesperado. Int�ntelo de nuevo.", Toast.LENGTH_LONG)
						.show();
			}


			if(passValido){

				boolean showSettingsAlert = SesionUsuario.iniciarSesion(this, usu);

				if(!showSettingsAlert){
					final Intent inte = new Intent(this, AplicacionActivity.class);
					startActivity(inte);
					finish();
				}
			//PASS INVALIDO
			}else {
				Toast.makeText(getApplicationContext(),
						"Error: password o login inv�lido", Toast.LENGTH_LONG)
						.show();
			}

		//LOGIN INVALIDO
		} else {
			Toast.makeText(getApplicationContext(),
					"Error: login o password inv�lido", Toast.LENGTH_LONG)
					.show();
		}
	}

	public void onClickSincronizar(View v) {

		AplicacionActivity.dialog = new ProgressDialog(this);
		AplicacionActivity.onClickSincronizar();

		new SincronizacionAsyncTask(this).execute(
				DataBaseHelper.TABLA_ROL, 
				DataBaseHelper.TABLA_USUARIO,
				DataBaseHelper.TABLA_PERMISO,
				DataBaseHelper.TABLA_ROL_PERMISO,
				DataBaseHelper.TABLA_USUARIO_ROL,
				DataBaseHelper.TABLA_SERVICIO);
	}
}
