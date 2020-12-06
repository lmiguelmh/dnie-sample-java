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

This is a part of the presentation: ["El DNI Electrónico peruano v2 y sus aplicaciones"]([https://docs.google.com/presentation/d/1_RHp0y6HZW7RpaGEQK-18KtXAPjx3ZAcPiL7wCt03MU/edit?usp=sharing]) for ["I Congreso Internacional Peruano - Argentino de Ingeniería de Sistemas e Informática"](https://sistemas.edu.pe/copaisi/).
