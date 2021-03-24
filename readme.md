# Java samples for Peruvian eID applications

Basic samples for Peruvian eID (DNIe).

## PKI

Using Sun PKCS#11 cryptographic provider to load OpenSC's Cryptoki library.
First install OpenSC. OpenSC is compatible with DNIe v1. 

**Note.** This sample works only for DNIe v1. We hope RENIEC will launch a PKCS#11 library for DNIe v2, or the community to make one for OpenSC.

## ICAO

Using JMRTD library to do BAC authentication and read ICAO DG1 and ICAO DG2. 
BAC authentication is compatible with both DNIe v1 and DNIe v2.
PACE authentication only works for DNIe v2.

This is a part of the presentation: ["El DNI Electrónico peruano v2 y sus aplicaciones"](El%20DNI%20Electr%C3%B3nico%20peruano%20v2%20y%20sus%20aplicaciones.pdf) for ["I Congreso Internacional Peruano - Argentino de Ingeniería de Sistemas e Informática"](https://sistemas.edu.pe/copaisi/). You can see the presentation in: https://youtu.be/XXlVS9I2toY
