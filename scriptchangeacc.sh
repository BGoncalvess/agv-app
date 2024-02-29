#!/bin/bash

echo "Escolha o user"
echo "-----------------"
echo "D - Deloitte"
echo "P - Pessoal"
echo "-----------------"

read -p "Input: " user

if [[ $user == "D" ]]; then
    echo " --------- Making changes for work account --------- "
    git config --global user.name "brunoadgoncalves"
    git config --global user.email "brgoncalves@deloitte.pt"
    eval "$(ssh-agent -s)"
    ssh-add ~/.ssh/id_ed25519_work
    echo " --------- Changes made --------- "
elif [[ $user == "P" ]]; then
    echo " --------- Making changes for personal account --------- "
    git config --global user.name "Bruno Gonçalves"
    git config --global user.email "Goncalves02Bruno@gmail.com"
    eval "$(ssh-agent -s)"
    ssh-add ~/.ssh/id_ed25519_personal
    echo " --------- Changes made --------- "
else
    echo " --------- Invalid input --------- "
fi
