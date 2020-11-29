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

(defrule SUEchases
	(SUE (run false)) => (assert (ACTION (id SUEchases))))

(defrule SUEprotects
	(SUE (canProtectAlly true)) 
	=> 
	(assert (ACTION (id SUEprotects) (info "Un aliado esta en apuros --> voy a salvarlo") )))	

(defrule SUEseeksProtection
	(SUE seekProtection true)
	=>
	(assert (ACTION (id SUEgoToActive) (info "soy débil y hay alguien que me proteja --> me acerco a él) )))

(defrule BLINKYrunsAway
	(SUE (run true)) =>  (assert (ACTION (id SUErunsAway))))
