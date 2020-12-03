;FACTS ASSERTED BY GAME INPUT
(deftemplate BLINKY
	(slot strong (type SYMBOL))	
	(slot canSecurePPill (type SYMBOL))	
	(slot canProtectAlly (type SYMBOL))	

	(slot seekProtection (type SYMBOL))
)
		
(deftemplate INKY
	(slot strong (type SYMBOL))	
	(slot canSecurePPill (type SYMBOL))	
	(slot canProtectAlly (type SYMBOL))	

	(slot seekProtection (type SYMBOL))
)

(deftemplate PINKY
	(slot strong (type SYMBOL))	
	(slot canSecurePPill (type SYMBOL))	
	(slot canProtectAlly (type SYMBOL))	

	(slot seekProtection (type SYMBOL))
)

(deftemplate SUE
	(slot strong (type SYMBOL))	
	(slot canSecurePPill (type SYMBOL))	
	(slot canProtectAlly (type SYMBOL))	

	(slot seekProtection (type SYMBOL))
)

(deftemplate CHECKMATE 
    (slot isCheckMate (type SYMBOL))
)
    
;DEFINITION OF THE ACTION FACT
(deftemplate ACTION
	(slot id) (slot info (default "")) 
) 

(defrule PINKYcheckmate
	(PINKY (strong true)) (CHECKMATE (isCheckMate true))
	=> 
	(assert (ACTION (id PINKYcheckmate) (info "checkmate --> checkmate") )))	

(defrule PINKYprotects
	(PINKY (strong true)) (PINKY (canProtectAlly true))
	=> 
	(assert (ACTION (id PINKYprotects) (info "puedo proteger aliado --> protejo") )))	

(defrule PINKYsecure
	(PINKY (strong true)) (PINKY (canSecurePPill true))
	=> 
	(assert (ACTION (id PINKYsecure) (info "puedo asegurar ppill --> la aseguro") )))	

(defrule PINKYchase
	(PINKY (strong true))
	=> 
	(assert (ACTION (id PINKYchase) (info "no puedo hacer nada fancy --> le persigo") )))	

(defrule PINKYseeksProtection
	(PINKY (strong false)) (PINKY (seekProtection true))
	=>
	(assert (ACTION (id PINKYseeksProtection) (info "soy debil y alguien puede protegerme --> me acerco a el") )))

(defrule PINKYrunsAway
	(PINKY (strong false))
	=>  
	(assert (ACTION (id PINKYrunsAway) (info "soy debil --> huyo") )))
	

	