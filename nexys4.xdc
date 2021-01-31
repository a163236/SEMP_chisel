set_property IOSTANDARD LVCMOS33 [get_ports clock]
set_property PACKAGE_PIN E3 [get_ports clock]
create_clock -name clock -period 2.0 [get_ports clock]