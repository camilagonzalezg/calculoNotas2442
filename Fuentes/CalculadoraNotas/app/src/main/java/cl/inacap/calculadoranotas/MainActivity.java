package cl.inacap.calculadoranotas;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import cl.inacap.calculadoranotas.dto.Nota;

public class MainActivity extends AppCompatActivity {

    //Porcentaje
    private int porcentajeActual=0;

    //Lista de notas
    private List<Nota> notas = new ArrayList<>();

    //EditText y ListView
    private EditText notaTxt;
    private EditText porcentajeTxt;
    private ListView notasLv;

    private ArrayAdapter<Nota> notasAdapter;

    private Button agregarBtn;
    private Button limpiarBtn;
    private LinearLayout promedioLl;
    private TextView promedioTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.promedioLl = findViewById(R.id.promedioLl);
        this.promedioTxt = findViewById(R.id.promedioTxt);
        this.notaTxt = findViewById(R.id.notaTxt);
        this.porcentajeTxt = findViewById(R.id.porcentajeTxt);
        this.notasLv = findViewById(R.id.notasLv);

        this.notasAdapter = new ArrayAdapter<>(this
                , android.R.layout.simple_list_item_1, notas);

        this.notasLv.setAdapter(notasAdapter);
        this.agregarBtn = findViewById(R.id.agregarBtn);
        this.limpiarBtn = findViewById(R.id.limpiarBtn);

        this.limpiarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1.Limpiar Edit Texts
                notaTxt.setText("");
                promedioTxt.setText("");
                porcentajeTxt.setText("");
                //2.Hacer invisible el layout de resultados
                promedioLl.setVisibility(View.INVISIBLE);
                //3. Limpiar la lista de notas
                notas.clear();
                //4. Notificar al data adapter para que se limpie
                notasAdapter.notifyDataSetChanged();
                //5. Dejar en 0 el porcentaje actual
                porcentajeActual = 0;
            }
        });

        //Listener:
        this.agregarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //Generar lista de errores para validar
            List<String> errores = new ArrayList<>();

            //guardar nota y porcentaje en variable
                double nota= 0;
                int porcentaje =0;

                //nota
                try {
                    nota = Double.parseDouble(notaTxt.getText().toString());
                    if(nota < 1.0 || nota > 7.0){
                        throw new NumberFormatException();
                    }
                }catch (NumberFormatException ex){
                    errores.add("La nota es un valor numero entre 1.0 y 7.0");
                }

                //porcentaje
                try {
                    porcentaje=Integer.parseInt(porcentajeTxt.getText().toString());
                    if (porcentaje <1 || porcentaje >100){
                        throw new NumberFormatException();
                    }
                }catch(NumberFormatException ex){
                    errores.add("El porcentaje debe ser un numero entre 1 y 100");
                }
                //Ver si lista está vacía, si es así, generar nota
                if (errores.isEmpty()){

                    if(porcentajeActual + porcentaje>100){
                        Toast.makeText(MainActivity.this ,
                                "No se uede sumar más de 100",
                                Toast.LENGTH_SHORT).show();
                    }else {
                        Nota n = new Nota();
                        n.setValor(nota);
                        n.setPorcentaje(porcentaje);
                        notas.add(n);

                        notasAdapter.notifyDataSetChanged();//decir al adaptador que se actualice
                        //Incrementar el porcentaje
                        porcentajeActual += porcentaje;
                        mostrarPromedio();
                    }
                }else{
                    //si no está vacía, muestro errores
                    mostrarErrores(errores);
                }
            }
        });
    }

    private void mostrarPromedio() {
        //0. Calcular el promedio
        double promedio =0;
        for(Nota n: notas){
            promedio+= n.getValor() * n.getPorcentaje()/100; // 6.5 50%
        }
        //1. Mostrar el promedio en el TextView
        this.promedioTxt.setText(String.format("%.2f",promedio)); //TODO: Y los decimales?
        //2. Colorear el TextView en funcion del promedio
        if(promedio < 4.0){
            //Colorear el TextView en rojo
            this.promedioTxt.setTextColor(ContextCompat.getColor(this
                    , R.color.colorRojo));
        } else {
            //Colorear el TextView en color pulento
            this.promedioTxt.setTextColor(ContextCompat.getColor(this
                    , R.color.colorVerde));
        }
        //3. Hacer visible el layout
        this.promedioLl.setVisibility(View.VISIBLE);
    }

private void mostrarErrores(List<String> errores){
        String mensaje = "";
        for(String e: errores){
            mensaje += "-" + e + "\n"; //Genera errores por linea
        }
        //Mostrar en un mensaje de alerta
    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        //chaining
        alertBuilder.setTitle("Error de validacion") //define el titulo
                .setMessage(mensaje) //define el contenido
                .setPositiveButton("Aceptar",null)//agrega boton aceptar
                .create() // crea el alert
                .show(); // lo muestra
}
}