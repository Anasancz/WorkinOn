@echo off
title Backend Tradicional Java Puro + MVC (WorkinON)
echo Compilando codigo Java...
javac -encoding UTF-8 -cp "lib/gson-2.10.1.jar;lib/mysql-connector-j-8.3.0.jar;lib/jbcrypt-0.4.jar" -d bin src/main/java/com/workinon/modelo/*.java src/main/java/com/workinon/modelo/evaluacion/*.java src/main/java/com/workinon/repositorio/*.java src/main/java/com/workinon/vista/*.java src/main/java/com/workinon/controlador/*.java src/main/java/com/workinon/*.java
if %errorlevel% neq 0 (
    echo Error de compilacion.
    pause
    exit /b %errorlevel%
)
echo Iniciando servidor HTTP en puerto 8080...
java -cp "bin;lib/gson-2.10.1.jar;lib/mysql-connector-j-8.3.0.jar;lib/jbcrypt-0.4.jar" com.workinon.MainBackendTradicional
pause
