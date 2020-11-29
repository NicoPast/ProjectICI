(deftemplate BLINKY
	(slot run (type SYMBOL)))
	(slot seekProtection (type SYMBOL))	
	(slot canProtectAlly (type SYMBOL))	

	
(deftemplate INKY
	(slot run (type SYMBOL)))
	(slot seekProtection (type SYMBOL))
	(slot canProtectAlly (type SYMBOL))	
	
(deftemplate PINKY
	(slot run (type SYMBOL)))
	(slot seekProtection (type SYMBOL))	
	(slot canProtectAlly (type SYMBOL))	

(deftemplate SUE
	(slot run (type SYMBOL)))
	(slot seekProtection (type SYMBOL))	
	(slot canProtectAlly (type SYMBOL))	
	
(deftemplate MSPACMAN 
    (slot mindistancePPill))
    
(deftemplate ACTION
	(slot id))   

(defrule PINKYchases
	(PINKY (run false)) => (assert (ACTION (id PINKYchases))))

(defrule PINKYprotects
	(PINKY (canProtectAlly true)) 
	=> 
	(assert (ACTION (id PINKYprotects) (info "Un aliado esta en apuros --> voy a salvarlo") )))	


(defrule PINKYseeksProtection
	(PINKY seekProtection true)
	=>
	(assert (ACTION (id PINKYgoToActive) (info "soy débil y hay alguien que me proteja --> me acerco a él) )))

(defrule BLINKYrunsAway
	(PINKY (run true)) =>  (assert (ACTION (id PINKYrunsAway))))

