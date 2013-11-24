node sling-metrics {
  package { [ 'graphite-web', 'python-carbon']:
    ensure => installed
  }

  exec { 'Create DB':
    cwd     => '/tmp',
    command => 'echo no | python /usr/lib/python2.6/site-packages/graphite/manage.py syncdb',
    creates => '/var/lib/graphite-web/graphite.db',
    path    => [ '/bin', '/usr/bin' ],
    user    => 'apache',
    require => Package['graphite-web']
  }

  service { 'carbon-cache':
    ensure  => 'running',
    enable  => true,
    require => Package['python-carbon']
  }

  service { 'httpd':
    ensure  => 'running',
    enable  => true,
    require => Exec['Create DB']
  }
}